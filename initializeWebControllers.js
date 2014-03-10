/**
 * Loads the WebSiteControllers props in the mongoDB
 */

db = connect("192.168.0.62:27017/dbNews");

var controllers = new Array(
  'BariToday',
  'BrindisiLibera',
  'BrindisiReport',
  'Corriere',
  'GiornaleDiPuglia',
  'GoBari',
  'LeccePrima',
  'NewsPuglia',
  'Puglia24News',
  'QuotidianoDiPuglia',
  'Repubblica',
  'SenzaColonne'
);

// delete current controllers
db.webControllers.drop();

// add all controllers but disable them
for (var w in controllers) {
  var controller = {
    props: controllers[w] + 'WebSiteController',
    enabled: 0
  };

  db.webControllers.insert(controller);
}

// enable controllers
db.webControllers.update({ props: 'BariTodayWebSiteController' }, { $set: { enabled: 1 } })