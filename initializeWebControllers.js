/**
 * Loads the WebSiteControllers props in the mongoDB
 */

db = connect("10.1.0.62:27017/dbNews");

var controllers = [
  'BariToday',
  'BrindisiLibera',
  'BrindisiReport',
  'CorriereSalentino',
  'Corriere',
  'GiornaleDiPuglia',
  'GoBari',
  'LeccePrima',
  'NewsPuglia',
  'Puglia24News',
  'QuotidianoDiPuglia',
  'Repubblica',
  'SenzaColonne'];

// delete current controllers
db.webControllers.drop();

// add all controllers but disable them
for (var w in controllers) {
  var controller = {
    controllerName: controllers[w] + 'WebSiteController',
    stopUrls: [],
    enabled: false
  };

  db.webControllers.insert(controller);
}

// enable controllers
db.webControllers.update(
    { controllerName: "SenzaColonneWebSiteController" },
    { $set: { enabled: true } },
    { upsert: false, multi: true }
)
